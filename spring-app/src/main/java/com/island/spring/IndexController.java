package com.island.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class IndexController {

    @GetMapping("/")
    public List<User> getUsers() {
        System.out.println("some body call me");
        return Arrays.stream(new String[]{"Geordie","Admin"}).map(User::new).collect(Collectors.toList());
    }

    static class User {
        private String name ;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
