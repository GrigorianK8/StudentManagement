package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Lesson;

import com.example.studentmanagement.entity.User;
import com.example.studentmanagement.entity.UserType;
import com.example.studentmanagement.repository.LessonsRepository;
import com.example.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class LessonsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LessonsRepository lessonsRepository;

    @Value("${picture.upload.directory}")
    private String uploadDirectory;

    @GetMapping("/lessons")
    public String lessonsPage(ModelMap modelMap) {
        modelMap.addAttribute("lessons", lessonsRepository.findAll());
        return "lessons";
    }

    @GetMapping("/lessons/add")
    public String addLessonsPage(ModelMap modelMap) {
        modelMap.addAttribute("lessons", lessonsRepository.findAll());

        List<User> teachers = userRepository.findByUserType(UserType.TEACHER);
        modelMap.addAttribute("teachers", teachers);

        return "addLessons";
    }

    @PostMapping("/lessons/add")
    public String addLesson(@ModelAttribute Lesson lesson) {
        lessonsRepository.save(lesson);
        return "redirect:/lessons";
    }

    @PostMapping("/lessons/update")
    public String updateLesson(@ModelAttribute Lesson lesson) {
        lessonsRepository.save(lesson);
        return "redirect:/lessons";
    }

    @GetMapping("/lessons/update/{id}")
    public String updateLessonPage(@PathVariable("id") int id, ModelMap modelMap) {
        Optional<Lesson> lessonOptional = lessonsRepository.findById(id);
        if (lessonOptional.isPresent()) {
            Lesson lesson = lessonOptional.get();
            modelMap.addAttribute("lesson", lesson);

            List<User> teachers = userRepository.findByUserType(UserType.TEACHER);
            modelMap.addAttribute("teachers", teachers);

            return "updateLesson";
        } else {
            return "redirect:/lessons";
        }
    }

    @GetMapping("/lessons/delete/{id}")
    public String deleteLesson(@PathVariable("id") int id) {
        lessonsRepository.deleteById(id);
        return "redirect:/lessons";
    }
}
