package com.acezhhh.common.vo;

import com.acezhhh.common.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author acezhhh
 * @date 2022/1/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageVo {

    private MessageTypeEnum type;

    private Object data;

}
