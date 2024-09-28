package top.kelton.llm;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson2.JSON;

import org.checkerframework.checker.units.qual.s;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabCommitDiff;
import org.gitlab.api.models.GitlabMergeRequest;
import org.gitlab.api.models.GitlabNote;
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
import java.util.List;

public class QwenTest {

    public static void callWithMessage(){
          Generation generation = new Generation();
          List<Message> messages = new ArrayList<>();

          // Get code changes
          GitlabMergeRequest mergeRequest = getMergeRequestChanges(2,2);
          System.out.println(JSON.toJSON(mergeRequest));

          GitlabCommitDiff commitdiff = mergeRequest.getChanges().get(0);
          String diff = commitdiff.getDiff();

          Message systemMsg = Message.builder().role(Role.SYSTEM.getValue()).content("you are a helpfull code assistant").build();
          Message userMsg = Message.builder().role(Role.USER.getValue()).content("对以下gitlab的merge request只做变更前后代码做code review, 返回格式按照：代码评分，代码建议，代码: " + diff).build();

          messages.add(systemMsg);
          messages.add(userMsg);

          GenerationParam param = GenerationParam.builder().model(Generation.Models.QWEN_TURBO).messages(messages)
                     .resultFormat(GenerationParam.ResultFormat.MESSAGE).build();

          GenerationResult result = generation.call(param);
          System.out.println(JSON.toJSON(result));

          // push the llm comments to the MR
          String comments = result.getOutput().getChoices().get(0).getMessage().getContent();
          System.out.println(comments);

          createNote(mergeRequest, comments);
          GitlabAPI gitlabApi = getGitlabAPI();

          gitlabApi.createCommitComment(mergeRequest.getIid(), mergeRequest.getSha(), comments, commitdiff.getNewPath(), "19", "new");  // 19 ?

    }

    public static GitlabMergeRequest getMergeRequestChanges(Serializable projectId, Integer mergeRequestIid){

        GitlabAPI gitlabApi = getGitlabAPI();

        GitlabMergeRequest mergeRequestChanges = gitlabApi.getMergeRequestChanges(projectId, mergeRequestIid);
        System.out.println(JSON.toJSONString(mergeRequestChanges.getChanges()));

        return mergeRequestChanges;

    }

    public static GitlabAPI getGitlabAPI(){
        String hostUrl = "http://localhost:8200";
        String token = "";
        GitlabAPI gitlabApi = GitlabAPI.connect(hostUrl, token);
        return gitlabApi;
    }

    public static GitlabNote createNote(GitlabMergeRequest mergeRequest, String message){
        GitlabAPI gitlabApi = getGitlabAPI();
        GitlabNote gitlabNote = gitlabApi.createNote(mergeRequest, message);
        System.out.println(JSON.toJSON(gitlabNote));
        return gitlabNote;
    }
    
}
