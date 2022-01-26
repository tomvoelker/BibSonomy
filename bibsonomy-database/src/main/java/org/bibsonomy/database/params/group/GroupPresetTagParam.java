package org.bibsonomy.database.params.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPresetTagParam {

    private int groupId;
    private String groupName;
    private String tagName;
    private String tagDescription;

}
