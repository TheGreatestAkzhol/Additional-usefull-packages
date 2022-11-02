package ru.alishev.springcourse.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.alishev.springcourse.dao.PersonDao;
import ru.alishev.springcourse.model.Person;
import ru.alishev.springcourse.util.PersonValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {
    @Autowired
    private PersonDao personDao;
    private PersonValidator personValidator;
    @Autowired
    public PeopleController(PersonDao personDao,PersonValidator personValidator) {
        this.personValidator = personValidator;
        this.personDao = personDao;
    }

    @GetMapping()
    public String index(Model model){
        //Получим всех людей из ДАО и передадим на отображение в представлении
       model.addAttribute("people",personDao.index());
        return "people/index";

    }
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id,Model model){
        //Получим одного человека по айди из дао и передадим на отображение
        model.addAttribute("person",personDao.show(id));
        return "people/show";
    }
    @GetMapping("/new")
    //можно добавить modelattribute))
    public String newPerson(Model model){
        model.addAttribute("person",new Person());
        return "people/new";
    }
    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person,bindingResult);
        if(bindingResult.hasErrors()){
            return "people/new";
        }
        personDao.save(person);
        return "redirect:/people";
    }
    @GetMapping("/{id}/edit")
    public String edit(Model model,@PathVariable("id") int id){
        model.addAttribute("person",personDao.show(id));
        return "/people/edit";
    }
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,BindingResult bindingResult,  @PathVariable("id") int id){
        personValidator.validate(person,bindingResult);
        if(bindingResult.hasErrors()){
            return "people/edit";
        }
        personDao.update(id,person);
        return "redirect:/people";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        personDao.delete(id);
        return "redirect:/people";
    }
}
