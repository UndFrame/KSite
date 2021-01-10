package ru.istu.b1978201.KSite.uploadingfiles;

/**
 * Ошибка которая возникает когда происходит сбой во время загрузки файла, в часности иконки
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}