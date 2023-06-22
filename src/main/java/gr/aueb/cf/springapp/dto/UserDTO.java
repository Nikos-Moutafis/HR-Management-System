package gr.aueb.cf.springapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;

    @Size(min = 8, message = "Password must have at least {min} characters")
    private String password;
}
