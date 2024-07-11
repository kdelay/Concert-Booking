package booking.api.waiting.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserDummy {

    public static List<User> getUserList() {
        List<User> userList = new ArrayList<>();
        BigDecimal amount = BigDecimal.ZERO;
        for (int i = 0; i < 10; i ++) {
            userList.add(
                User.create((long) (i+1), amount)
            );
        }
        return userList;
    }
}