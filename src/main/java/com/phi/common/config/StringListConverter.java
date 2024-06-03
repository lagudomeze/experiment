package com.phi.common.config;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class StringListConverter extends AbstractHttpMessageConverter<StringListConverter.StringList> {

    public static final class StringList extends ArrayList<String> {

    }

    @Override
    protected boolean supports(@NonNull Class<?> clazz) {
        return StringList.class.equals(clazz);
    }

    @Override
    protected boolean canRead(@Nullable MediaType mediaType) {
        return MediaType.APPLICATION_OCTET_STREAM.equals(mediaType);
    }


    @Override
    protected @NonNull StringList readInternal(@NonNull Class<? extends StringList> clazz, @NonNull HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        StringList result = new StringList();
        byte[] bytes = inputMessage.getBody().readAllBytes();
        result.addAll(Arrays.asList(new String(bytes, StandardCharsets.UTF_8).split(",")));
        return result;
    }

    @Override
    protected void writeInternal(@NonNull StringList strings, @NonNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        String result = String.join(",", strings);
        outputMessage.getBody().write(result.getBytes());
    }
}
