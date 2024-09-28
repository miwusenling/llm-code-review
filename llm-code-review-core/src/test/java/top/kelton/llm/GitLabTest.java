package top.kelton.llm;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.fastjson2.JSON;

import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabCommitDiff;
import org.gitlab.api.models.GitlabMergeRequest;
import org.junit.Test;
import top.kelton.llm.component.git.GitCommand;
import top.kelton.llm.component.llmqa.ChatGLM;
import top.kelton.llm.component.llmqa.IAISession;
import top.kelton.llm.component.llmqa.dto.ChatCompletionRequestDTO;
import top.kelton.llm.component.llmqa.dto.ChatCompletionSyncResponseDTO;
import top.kelton.llm.component.push.PushPlus;
import top.kelton.llm.component.push.TemplateKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GitLabTest {

    public static void callWithMessage(){

          String hosturl = "";
          String token = "";
          
          GitlabAPI gitlabAPI = GitlabAPI.connect(hosturl, token);

          GitlabMergeRequest mergeRequest = gitlabAPI.getMergeRequestChanges(2,2);
          System.out.println(mergeRequest.getChanges()); 

    }
    
}
