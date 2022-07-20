package ru.land.service.enums;

public enum StaticStrings {

    BOT_USERNAME("CourseOfPythonTODObot"),
    BOT_TOKEN("5359035126:AAEC7QZ8m2R9e72x43xFTLg1PblZKISRr3A"),
    DATE_IS_SET("Дата установлена"),
    NONE_BRIGADE("Не указан номер бригады. Для регистрации воспользуйтесь командой: "),
    TIME_ZONE("Europe/Moscow"),
    UNKNOWN_COMMAND("Неизвестная команда!"),
    YEAR_AND_MONTH_MAPPER("yyyy.MM"),
    YEAR_MONTH_AND_DAY_MAPPER("yyyy.MM.dd");

    private final String value;

    StaticStrings(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }
}
