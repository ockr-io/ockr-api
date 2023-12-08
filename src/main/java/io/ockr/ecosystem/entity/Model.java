package io.ockr.ecosystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "model")
@Builder
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;
    private String url;
    private Integer port;

    public Model(String name, String url, Integer port) {
        this.name = name;
        this.url = url;
        this.port = port;
    }
}
