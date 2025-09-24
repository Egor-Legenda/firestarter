package itmo.programming.common;

/**
 * Перечисление статусов обработки файлов.
 */
public enum Status {
    RECEIVED,                     // Файл принят
    PRIMARY_VALIDATION_SUCCESS,   // Первичная валидация успешна
    PRIMARY_VALIDATION_FAILED,    // Первичная валидация неуспешна
    SECONDARY_VALIDATION_SUCCESS, // Вторичная валидация успешна
    SECONDARY_VALIDATION_FAILED,  // Вторичная валидация неуспешна
    SECONDARY_VALIDATION_ERROR,   // Файл уже был загружен
    SECONDARY_VALIDATION_STARTED, // Начало второй валидации
    UPLOAD_ERROR                  // Ошибка загрузки
}
