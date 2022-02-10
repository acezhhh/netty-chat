package com.acezhhh.common.vo;

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
public class ChatVo {

    private UserVo userVo;

    private String content;

    private String targetChannelId;

    private String sourceChannelId;

}
