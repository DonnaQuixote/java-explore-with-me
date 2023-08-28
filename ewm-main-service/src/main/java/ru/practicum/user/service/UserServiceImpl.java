package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        if (ids == null) return repository
                .findAll(PageRequest.of(from / size, size))
                .map(UserMapper::toUserDto)
                .toList();

        return repository.findAllById(ids).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto postUser(NewUserRequest request) {
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(request)));
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(String.format("User with id=%d was not found", userId));
        }
    }
}