package by.sam_solutions.findtrip.exception;

import by.sam_solutions.findtrip.controller.dto.UserDTO;

public class EditUsersParametersExistException extends RuntimeException {

    UserDTO userDTO;

    public EditUsersParametersExistException(String message, UserDTO userDTO) {
        super(message);
        this.userDTO = userDTO;
    }
}
