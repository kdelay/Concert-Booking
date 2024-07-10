package booking.api.concert.domain;

import lombok.Getter;

@Getter
public class Concert {

    private final Long id;
    private final String name;
    private final String host;

    public Concert(Long id, String name, String host) {
        if (name == null) throw new IllegalArgumentException("[Concert - name] is null");
        if (host == null) throw new IllegalArgumentException("[Concert - host] is null");
        this.id = id;
        this.name = name;
        this.host = host;
    }

    public static Concert create(Long id, String name, String host) {
        return new Concert(id, name, host);
    }
}