package controllers.exceptions;

/*
"Исключения вида IOException нужно отлавливать внутри метода save и выкидывать собственное непроверяемое исключение ManagerSaveException."
Вот тут не совсем поняла, правильно ли я реализовала исключение. По заданию оно указано как непроверяемое, а IOException из проверяемого типа
 */
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }
}
