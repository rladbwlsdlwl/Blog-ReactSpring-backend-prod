package board.server.app.tags.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Entity
@Table(name = "tags_table")
public class Tags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;


    public static Tags of(String name) {
        return Tags.builder()
                .name(name)
                .build();
    }

    public static Tags of(String name, Long id){
        return Tags.builder()
                .id(id)
                .name(name)
                .build();
    }
}
