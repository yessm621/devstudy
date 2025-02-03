package me.devstudy.account.domain;

import me.devstudy.domain.support.ListStringConverter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConverterTest {

    @Test
    void ListStringConverterTest() {
        ListStringConverter converter = new ListStringConverter();
        List<String> inputList = List.of("hello", "java", "world");

        String convertedToDb = converter.convertToDatabaseColumn(inputList);
        List<String> convertedToEntity = converter.convertToEntityAttribute(convertedToDb);

        assertThat(convertedToDb).isEqualTo("hello,java,world");
        assertThat(convertedToEntity).containsExactly("hello", "java", "world");
    }
}
