package cn.ctrlcv.im.serve.user.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: ImportUserResp
 * Class Description: 导入用户后，接口的返回数
 *
 * @author liujm
 * @date 2023-02-07
 */
@Data
public class ImportUserResp {

    private List<String> successId;
    private List<String> errorId;
}
