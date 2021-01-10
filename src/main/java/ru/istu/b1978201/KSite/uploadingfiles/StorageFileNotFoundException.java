package ru.istu.b1978201.KSite.uploadingfiles;

/**
 *  Ошибка возникающяя, когда пытаются получить файл которого не существет.
 */
public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

