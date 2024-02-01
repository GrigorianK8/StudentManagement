package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.User;
import com.example.studentmanagement.entity.UserType;
import com.example.studentmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class TeacherController {

    @Autowired
    private UserRepository userRepository;

    @Value("${picture.upload.directory}")
    private String uploadDirectory;

    @GetMapping("/teachers")
    public String teachersPage(ModelMap modelMap) {
        List<User> teachers = userRepository.findByUserType(UserType.TEACHER);
        modelMap.addAttribute("teachers", teachers);
        return "teachers";
    }

    @GetMapping("/teachers/add")
    public String addTeacherPage(ModelMap modelMap) {
        modelMap.addAttribute("teachers", userRepository.findAll());
        return "addTeacher";
    }

    @PostMapping("/teachers/add")
    public String addTeacher(@ModelAttribute User teacher,
                             @RequestParam("picture") MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(uploadDirectory, picName);
            multipartFile.transferTo(file);
            teacher.setPicName(picName);
        }
        teacher.setUserType(UserType.TEACHER);
        userRepository.save(teacher);
        return "redirect:/teachers";
    }

    @GetMapping("/teachers/update/{id}")
    public String updateTeacherPage(@PathVariable("id") int id, ModelMap modelMap) {
        Optional<User> teacherOptional = userRepository.findById(id);
        if (teacherOptional.isPresent()) {
            modelMap.addAttribute("teacher", teacherOptional.get());
            return "updateTeacher";
        } else {
            return "redirect:/teachers";
        }
    }

    @PostMapping("/teachers/update")
    public String updateTeacher(@ModelAttribute User teacher,
                                @RequestParam("picture") MultipartFile multipartFile,
                                ModelMap modelMap) throws IOException {
        if (!multipartFile.isEmpty()) {
            String picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(uploadDirectory, picName);
            multipartFile.transferTo(file);
            teacher.setPicName(picName);
        }
        userRepository.save(teacher);

        List<User> teachers = userRepository.findByUserType(UserType.TEACHER);
        modelMap.addAttribute("teachers", teachers);
        return "redirect:/teachers";
    }

    @GetMapping("/teachers/image/delete")
    public String deleteTeacherImage(@RequestParam("id") int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/teachers";
        } else {
            User teacher = userOptional.get();
            String picName = teacher.getPicName();
            if (picName != null) {
                teacher.setPicName(null);
                userRepository.save(teacher);
                File file = new File(uploadDirectory, picName);
                if (file.exists()) {
                    file.delete();
                }
            }
            return "redirect:/teachers/update/" + teacher.getId();
        }
    }

    @GetMapping("/teachers/delete/{id}")
    public String deleteTeacher(@PathVariable("id") int id) {
        userRepository.deleteById(id);
        return "redirect:/teachers";
    }
}
