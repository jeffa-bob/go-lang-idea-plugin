package ro.redeul.google.go.intentions.parenthesis;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.intentions.Intention;

import static ro.redeul.google.go.intentions.parenthesis.ParenthesisUtil.getDeclaration;
import static ro.redeul.google.go.intentions.parenthesis.ParenthesisUtil.getRightParenthesis;
import static ro.redeul.google.go.intentions.parenthesis.ParenthesisUtil.hasOnlyOneDeclaration;

public class AddDeclarationParenthesesIntention extends Intention {
    @Override
    protected boolean satisfiedBy(PsiElement element) {
        return getRightParenthesis(element) == null && hasOnlyOneDeclaration(element);
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Project project, Editor editor)
            throws IncorrectOperationException {
        PsiElement declaration = getDeclaration(element);
        TextRange range = declaration.getTextRange();
        Document document = editor.getDocument();
        String text = "(\n    " + declaration.getText() + "\n)";
        document.replaceString(range.getStartOffset(), range.getEndOffset(), text);
    }
}
