package sumdu.edu.ua.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class WebArchitectureTest {

    @Test
    void servletsShouldNotDependOnPersistence() {
        JavaClasses imported = new ClassFileImporter().importPackages("sumdu.edu.ua");

        noClasses()
                .that().haveSimpleNameEndingWith("Servlet")
                .should().dependOnClassesThat()
                .resideInAPackage("..persistence..")
                .check(imported);
    }

    @Test
    void coreShouldNotDependOnWebOrPersistence() {
        JavaClasses imported = new ClassFileImporter().importPackages("sumdu.edu.ua");

        noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat().resideInAnyPackage("..web..", "..persistence..")
                .check(imported);
    }
}