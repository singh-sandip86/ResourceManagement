package com.rm.data.project

import com.rm.view.common.UIDropdownField
import com.rm.view.common.UIListField
import com.rm.view.common.UITextField

data class UISubProject(
    val subProjectId: UITextField = UITextField(),
    val subProjectName: UITextField = UITextField(),
    val subProjectDescription: UITextField = UITextField(),
    val subProjectState: UIDropdownField = UIDropdownField(),
    val subProjectScope: UIListField = UIListField(),
    val subProjectStartDate: UITextField = UITextField(),
    val subProjectEndDate: UITextField = UITextField(),
    val subProjectResourceTypeList: UIListField = UIListField(),
)
