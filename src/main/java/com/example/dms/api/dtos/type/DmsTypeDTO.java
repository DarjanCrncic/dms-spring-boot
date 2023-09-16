package com.example.dms.api.dtos.type;

import com.example.dms.api.dtos.BaseEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmsTypeDTO implements BaseEntityDTO {

	private Integer id;
	private LocalDateTime creationDate;
	private LocalDateTime modifyDate;

	private String typeName;
}
