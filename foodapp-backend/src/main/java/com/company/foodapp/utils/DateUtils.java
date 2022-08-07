package com.company.foodapp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateUtils {
    private SimpleDateFormat simpleDateFormat;

    @Autowired
    public DateUtils(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    public String getCurrentDate() {
        var date = new Date();
        var parsedDate = simpleDateFormat.format(date);

        return parsedDate;
    }
}
