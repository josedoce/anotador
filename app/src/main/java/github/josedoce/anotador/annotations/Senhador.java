package github.josedoce.anotador.annotations;

import org.jasypt.util.text.BasicTextEncryptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Senhador {
    public static <T> T createEncryptedModel(String password, T model) {
        try{
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
            textEncryptor.setPassword(password);

            for(Field field : model.getClass().getDeclaredFields()){
                Enc enc = (Enc) field.getAnnotation(Enc.class);
                if(enc != null){
                    String fieldName = toCapitalize(field.getName());
                    Method getter = model.getClass().getDeclaredMethod("get"+fieldName);

                    Object plainText = getter.invoke(model);
                    String textEncrypted = textEncryptor.encrypt((String)plainText);
                    Method setter = model.getClass().getDeclaredMethod("set"+fieldName, String.class);
                    setter.invoke(model, textEncrypted);
                }
            }
            return model;
        }catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return model;
        }

    }

    public static <T> void createDecryptedModel(String password, T model) {
        try{
            BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
            textEncryptor.setPassword(password);
            for(Field field : model.getClass().getDeclaredFields()){
                Enc enc = (Enc) field.getAnnotation(Enc.class);
                if(enc != null){
                    String fieldName = toCapitalize(field.getName());
                    Method getter = model.getClass().getDeclaredMethod("get"+fieldName);

                    Object encryptedText = getter.invoke(model);
                    String textEncrypted = textEncryptor.decrypt((String) encryptedText);
                    Method setter = model.getClass().getDeclaredMethod("set"+fieldName, String.class);
                    setter.invoke(model, textEncrypted);
                }

            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String toCapitalize(String string){
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
