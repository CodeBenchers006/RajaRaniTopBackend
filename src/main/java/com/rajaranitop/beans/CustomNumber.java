package com.rajaranitop.beans;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
public class CustomNumber {

    @Id
    private int customId;
    private int customNumberData;
}
