package com.example.todolist.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class TodoListService extends IntentService {

    public TodoListService() {
        super("TodoList Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //TODO add implementation to parse file
    }
}
