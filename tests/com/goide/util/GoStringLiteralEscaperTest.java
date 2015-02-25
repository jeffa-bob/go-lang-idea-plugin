/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.util;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoStringLiteral;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GoStringLiteralEscaperTest extends GoCodeInsightFixtureTestCase {

  private static String decodeRange(GoStringLiteral expr, TextRange range) {
    final StringBuilder builder = new StringBuilder();
    expr.createLiteralTextEscaper().decode(range, builder);
    return builder.toString();
  }

  public void testEscaperDecodeString() {
    final GoStringLiteral expr = createStringFromText("\\nfoo");
    assertNotNull(expr);
    assertEquals("fo", decodeRange(expr, TextRange.create(3, 5)));
    assertEquals("\n", decodeRange(expr, TextRange.create(1, 3)));
  }

  public void testEscaperDecodeRawString() {
    final GoStringLiteral expr = createRawStringFromText("\nfoo");
    assertNotNull(expr);
    assertEquals("fo", decodeRange(expr, TextRange.create(2, 4)));
    assertEquals("\n", decodeRange(expr, TextRange.create(1, 2)));
  }

  public void testEscaperOffsetInStringHost() {
    final GoStringLiteral expr = createStringFromText("\\nfoo");
    assertNotNull(expr);
    final LiteralTextEscaper<? extends PsiLanguageInjectionHost> escaper = expr.createLiteralTextEscaper();
    final TextRange newLineFoo = TextRange.create(1, 6);
    escaper.decode(newLineFoo, new StringBuilder());
    assertEquals(1, escaper.getOffsetInHost(0, newLineFoo));
    assertEquals(3, escaper.getOffsetInHost(1, newLineFoo));
    assertEquals(4, escaper.getOffsetInHost(2, newLineFoo));
    assertEquals(5, escaper.getOffsetInHost(3, newLineFoo));
    assertEquals(6, escaper.getOffsetInHost(4, newLineFoo));
    assertEquals(-1, escaper.getOffsetInHost(5, newLineFoo));
  }

  public void testEscaperOffsetInRawStringHost() {
    final GoStringLiteral expr = createRawStringFromText("\nfoo");
    assertNotNull(expr);
    final LiteralTextEscaper<? extends PsiLanguageInjectionHost> escaper = expr.createLiteralTextEscaper();
    final TextRange newLineFoo = TextRange.create(1, 5);
    escaper.decode(newLineFoo, new StringBuilder());
    assertEquals(1, escaper.getOffsetInHost(0, newLineFoo));
    assertEquals(2, escaper.getOffsetInHost(1, newLineFoo));
    assertEquals(3, escaper.getOffsetInHost(2, newLineFoo));
    assertEquals(4, escaper.getOffsetInHost(3, newLineFoo));
    assertEquals(5, escaper.getOffsetInHost(4, newLineFoo));
    assertEquals(-1, escaper.getOffsetInHost(5, newLineFoo));
  }

  public void testEscaperOffsetInStringHostSubString() {
    final GoStringLiteral expr = createStringFromText("\\nfoo");
    assertNotNull(expr);
    final LiteralTextEscaper<? extends PsiLanguageInjectionHost> escaper = expr.createLiteralTextEscaper();
    final TextRange fooOnly = TextRange.create(3, 6);
    escaper.decode(fooOnly, new StringBuilder());
    assertEquals(3, escaper.getOffsetInHost(0, fooOnly));
    assertEquals(4, escaper.getOffsetInHost(1, fooOnly));
    assertEquals(5, escaper.getOffsetInHost(2, fooOnly));
    assertEquals(6, escaper.getOffsetInHost(3, fooOnly));
    assertEquals(-1, escaper.getOffsetInHost(4, fooOnly));
  }

  public void testEscaperOffsetInRawStringHostSubString() {
    final GoStringLiteral expr = createRawStringFromText("\nfoo");
    assertNotNull(expr);
    final LiteralTextEscaper<? extends PsiLanguageInjectionHost> escaper = expr.createLiteralTextEscaper();
    final TextRange fooOnly = TextRange.create(2, 5);
    escaper.decode(fooOnly, new StringBuilder());
    assertEquals(2, escaper.getOffsetInHost(0, fooOnly));
    assertEquals(3, escaper.getOffsetInHost(1, fooOnly));
    assertEquals(4, escaper.getOffsetInHost(2, fooOnly));
    assertEquals(5, escaper.getOffsetInHost(3, fooOnly));
    assertEquals(-1, escaper.getOffsetInHost(4, fooOnly));
  }

  public void testEscaperOffsetInSingleCharString() {
    doSingleCharTest(createStringFromText("c"));
  }

  public void testEscaperOffsetInSingleCharRawString() {
    doSingleCharTest(createRawStringFromText("c"));
  }

  private static void doSingleCharTest(@NotNull GoStringLiteral expr) {
    final LiteralTextEscaper<? extends PsiLanguageInjectionHost> escaper = expr.createLiteralTextEscaper();
    final TextRange range = TextRange.create(1, 2);
    escaper.decode(range, new StringBuilder());
    assertEquals(1, escaper.getOffsetInHost(0, range));
    assertEquals(2, escaper.getOffsetInHost(1, range));
    assertEquals(-1, escaper.getOffsetInHost(2, range));
  }

  public void testEscaperOffsetInSingleEscapedCharString() {
    final GoStringLiteral expr = createStringFromText("\\n");
    assertNotNull(expr);
    final LiteralTextEscaper<? extends PsiLanguageInjectionHost> escaper = expr.createLiteralTextEscaper();
    final TextRange range = TextRange.create(1, 3);
    escaper.decode(range, new StringBuilder());
    assertEquals(1, escaper.getOffsetInHost(0, range));
    assertEquals(3, escaper.getOffsetInHost(1, range));
    assertEquals(-1, escaper.getOffsetInHost(2, range));
  }

  // region decode tests

  public void testDecodeEscapedString() {
    final GoStringLiteral expr = createStringFromText("\\t\\n\\b");
    assertNotNull(expr);
    String a = decodeRange(expr, TextRange.create(1, 7));
    assertEquals("\t\n\b", a);
  }

  public void testDecodeEscapedVerticalTabString() {
    final GoStringLiteral expr = createStringFromText("\\v");
    assertNotNull(expr);
    String a = decodeRange(expr, TextRange.create(1, 3));
    assertEquals("\013", a);
  }

  public void testDecodeEscapedBellString() {
    final GoStringLiteral expr = createStringFromText("\\a");
    assertNotNull(expr);
    String a = decodeRange(expr, TextRange.create(1, 3));
    assertEquals("\007", a);
  }

  public void testDecodeOctalCharString() {
    final GoStringLiteral expr = createStringFromText("\\011");
    assertNotNull(expr);
    String a = decodeRange(expr, TextRange.create(1, 5));
    assertEquals("\t", a);
  }

  public void testDecodeHexCharString() {
    final GoStringLiteral expr = createStringFromText("\\x41");
    assertNotNull(expr);
    String a = decodeRange(expr, TextRange.create(1, 5));
    assertEquals("A", a);
  }

  public void _testDecodeShortUnicodeCharString() {
    final GoStringLiteral expr = createStringFromText("\\u8a9e");
    assertNotNull(expr);
    String a = decodeRange(expr, TextRange.create(1, 7));
    assertEquals("語", a);
  }

  public void _testDecodeLongUnicodeCharString() {
    final GoStringLiteral expr = createStringFromText("\\U00008a9e");
    assertNotNull(expr);
    String a = decodeRange(expr, TextRange.create(1, 11));
    assertEquals("語", a);
  }

  public void testQuote() {
    final GoStringLiteral expr = createStringFromText("import \\\"fmt\\\"");
    assertNotNull(expr);
    assertEquals("\"fmt\"", decodeRange(expr, TextRange.create(8, 15)));
  }

  // endregion

  @NotNull
  private GoStringLiteral createStringFromText(@NotNull final String text) {
    return GoElementFactory.createStringLiteral(myFixture.getProject(), String.format(Locale.US, "\"%s\"", text));
  }

  @NotNull
  private GoStringLiteral createRawStringFromText(@NotNull final String text) {
    return GoElementFactory.createStringLiteral(myFixture.getProject(), String.format(Locale.US, "`%s`", text));
  }
}
