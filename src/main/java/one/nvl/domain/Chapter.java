package one.nvl.domain;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class Chapter {

    @JsonPropertyDescription("目录信息")
    private String catalogs;
    @JsonPropertyDescription("章节摘要内容")
    private String abstracts;
    @JsonPropertyDescription("章节正文内容")
    private String content;

}
