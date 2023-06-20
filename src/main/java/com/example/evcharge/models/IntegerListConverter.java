package com.example.evcharge.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Converter
    public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {
        private static final String SPLIT_CHAR = ",";

        @Override
        public String convertToDatabaseColumn(List<Integer> stringList) {
            return stringList != null ? String.join(",", stringList.stream().map(String::valueOf).collect(Collectors.toList())) : "";
        }

        @Override
        public List<Integer> convertToEntityAttribute(String string) {
            if(string !=null){
                List<String> stringList = Arrays.asList(string.split(SPLIT_CHAR));
                return stringList.stream().map(Integer::valueOf).collect(Collectors.toList());
            }
            return emptyList();
        }
    }