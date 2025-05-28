package one.nvl.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Deprecated
@Data
public class Catalog {

    @JsonPropertyDescription("卷ID，数字类型，比如：1,2,3。描述当前是第几卷")
    private Integer volumeId;
    @JsonPropertyDescription("卷名，比如第一卷 出场，第二卷 相识，第三卷 结缘。描述当前卷名")
    private String volumeName;
    @JsonPropertyDescription("章节ID，数字类型，比如：1,2,3,4,5。描述当前章节ID")
    private Integer id;
    @JsonPropertyDescription("章节标题")
    private String title;
    @JsonPropertyDescription("章节内容简介")
    private String abstracts;

    public String getAbstracts(Integer volumeId, Integer characterId) {
        return String.format("第%d卷 %s\n第%d章 %s\n%s", volumeId, volumeName, characterId, title, abstracts);
    }

    public String volumeName() {
        return String.format("第%d卷", getVolumeId());
    }

    public String characterName() {
        return String.format("第%d章", getId());
    }

}
