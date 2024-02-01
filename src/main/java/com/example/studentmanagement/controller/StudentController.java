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
public class StudentController {

    @Autowired
    private UserRepository userRepository;

    @Value("${picture.upload.directory}")
    private String uploadDirectory;

    @GetMapping("/students")
    public String studentsPage(ModelMap modelMap) {
        List<User> students = userRepository.findByUserType(UserType.STUDENT);
        modelMap.addAttribute("students", students);
        return "students";
    }

    @GetMapping("/students/add")
    public String addStudentPage(ModelMap modelMap) {
        modelMap.addAttribute("students", userRepository.findAll());
        return "addStudent";
    }

    @PostMapping("/students/add")
    public String addStudent(@ModelAttribute User student,
                             @RequestParam("picture") MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(uploadDirectory, picName);
            multipartFile.transferTo(file);
            student.setPicName(picName);
        }
        student.setUserType(UserType.STUDENT);
        userRepository.save(student);
        return "redirect:/students";
    }

    @GetMapping("students/update/{id}")
    public String updateStudentPage(@PathVariable("id") int id, ModelMap modelMap) {
        Optional<User> studentOptional = userRepository.findById(id);
        if (studentOptional.isPresent()) {
            modelMap.addAttribute("student", studentOptional.get());
            return "updateStudent";
        } else {
            return "redirect:/students";
        }
    }

    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute User student,
                                @RequestParam("picture") MultipartFile multipartFile,
                                ModelMap modelMap) throws IOException {
        if (!multipartFile.isEmpty()) {
            String picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(uploadDirectory, picName);
            multipartFile.transferTo(file);
            student.setPicName(picName);
        }
        userRepository.save(student);

        List<User> students = userRepository.findByUserType(UserType.STUDENT);
        modelMap.addAttribute("students", students);

        return "redirect:/students";
    }

    @GetMapping("/students/image/delete")
    public String deleteStudentImage(@RequestParam("id") int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/students";
        } else {
            User student = userOptional.get();
            String picName = student.getPicName();
            if (picName != null) {
                student.setPicName(null);
                userRepository.save(student);
                File file = new File(uploadDirectory, picName);
                if (file.exists()) {
                    file.delete();
                }
            }
            return "redirect:/students/update/" + student.getId();
        }
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable("id") int id) {
        userRepository.deleteById(id);
        return "redirect:/students";
    }
}
