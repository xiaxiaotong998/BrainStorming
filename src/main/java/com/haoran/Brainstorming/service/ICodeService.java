package com.haoran.Brainstorming.service;

import com.haoran.Brainstorming.model.Code;

public interface ICodeService {
    Code selectByCode(String _code);

    Code selectNotUsedCode(Integer userId, String email, String mobile);

    Code createCode(Integer userId, String email, String mobile);

    Code validateCode(Integer userId, String email, String mobile, String _code);

    void update(Code code);

    void deleteByUserId(Integer userId);
}
