package inspections;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface AnnotationsOwner {
  @NotNull
  AnnotationsOwner empty = annotation -> Optional.empty();

  @SuppressWarnings("StaticMethodNamingConvention")
  @NotNull
  public static AnnotationsOwner of(@NotNull PsiModifierListOwner element) {
    return Optional.ofNullable(element.getModifierList()).map(modifiers -> (AnnotationsOwner) new Wrapper(modifiers)).orElse(empty);
  }

  @NotNull
  Optional<PsiAnnotation> get(@NotNull Class<? extends Annotation> annotation);

  final class Wrapper implements AnnotationsOwner {
    @NotNull
    private final PsiModifierList modifiers;

    private Wrapper(@NotNull PsiModifierList modifiers) {
      this.modifiers = modifiers;
    }

    @NotNull
    @Override
    public Optional<PsiAnnotation> get(@NotNull Class<? extends Annotation> annotation) {
      return Optional.ofNullable(modifiers.findAnnotation(annotation.getCanonicalName()));
    }
  }
}
