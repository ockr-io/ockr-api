package io.ockr.ecosystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(unique = true)
    private String name;
    private String url;
    private Integer port;
}
