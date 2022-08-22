package github.josedoce.anotador;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.OrderWith;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import github.josedoce.anotador.annotations.Senhador;
import github.josedoce.anotador.model.Annotation;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void enc(){
        String
                text = "maria ferreira",
                password = "umasenha";

        Annotation pAnnotation = new Annotation();
        pAnnotation.setTitle(text);

        Senhador.createEncryptedModel(password, pAnnotation);
        assertNotEquals(text, pAnnotation.getTitle());

        Senhador.createDecryptedModel(password, pAnnotation);
        assertEquals(text, pAnnotation.getTitle());
    }


}
