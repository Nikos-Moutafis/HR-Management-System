package gr.aueb.cf.springapp.service;

import gr.aueb.cf.springapp.entity.User;

public interface IAutoLoginService {
     void autoLogin(User user, String rawPassword);
}
