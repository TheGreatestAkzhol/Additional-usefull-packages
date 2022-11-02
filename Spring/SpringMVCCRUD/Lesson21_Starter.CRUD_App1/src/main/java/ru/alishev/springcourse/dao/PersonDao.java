package ru.alishev.springcourse.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.alishev.springcourse.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PersonDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> index() {
        return jdbcTemplate.query("SELECT * FROM Person", new BeanPropertyRowMapper<>(Person.class));
    }
    public Optional<Person> show(String email){
        return jdbcTemplate.query("Select * from person where email=?",new Object[]{email},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
    }
    public Person show(int id) {
        return jdbcTemplate.query("SELECT * FROM Person Where id = ?", new Object[]{id}, new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny().orElse(null);
    }

    public void save(Person person) {
        jdbcTemplate.update("INSERT into person(name,age,email,address) values (?,?,?,?)", person.getName(), person.getAge(),
                person.getEmail(),person.getAddress());
    }

    public void update(int id, Person updatedPerson) {
        jdbcTemplate.update("UPDATE Person SET name= ?, age=?, email=?, address=? where id = ?", updatedPerson.getName(),
                updatedPerson.getAge(), updatedPerson.getEmail(),updatedPerson.getAddress(), id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM PERSON WHERE id = ?",id);

    }
    ////////////////////
    //Тестируем производительность пакетной вставки
    ////////////////
    public void testMultipleUpdate(){
        List<Person> people = create1000People();
        long before = System.currentTimeMillis();
        for(Person person: people){
            jdbcTemplate.update("INSERT into person values (?,?,?,?)",person.getId(), person.getName(), person.getAge(),
                    person.getEmail());
        }
        long after = System.currentTimeMillis();
        System.out.println("Time: "+(after-before));
    }
    private List<Person> create1000People(){
        List<Person> people = new ArrayList<>();
        for(int i = 0; i<1000; i++){
            people.add(new Person(i,"name"+i,30,"test"+i+"mail.ru","some address"));
        }
        return people;
    }
    public void testBatchUpdate(){
        List<Person> people = create1000People();
        long before = System.currentTimeMillis();
        jdbcTemplate.batchUpdate("INSERT into person values(?,?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1,people.get(i).getId());
                preparedStatement.setString(2,people.get(i).getName());
                preparedStatement.setInt(3,people.get(i).getAge());
                preparedStatement.setString(4,people.get(i).getEmail());
            }

            @Override
            public int getBatchSize() {
                return people.size();
            }
        });
        long after = System.currentTimeMillis();
        System.out.println("Time: "+(after-before));
    }

}
