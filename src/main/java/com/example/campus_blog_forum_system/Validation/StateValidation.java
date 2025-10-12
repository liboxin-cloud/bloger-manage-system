package com.example.campus_blog_forum_system.Validation;

import com.example.campus_blog_forum_system.anno.State;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StateValidation implements ConstraintValidator<State, String>
{
    @Override
    public boolean isValid(String state, ConstraintValidatorContext context)
    {
        if(state == null || state.trim().isEmpty())
        {
            return false;

        }
        if(state.equals("已发布") || state.equals("草稿"))
        {
            return true;
        }
        return false;
    }


}
