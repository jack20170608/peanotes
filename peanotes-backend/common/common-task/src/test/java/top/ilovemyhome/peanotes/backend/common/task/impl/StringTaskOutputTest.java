package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class StringTaskOutputTest {

    @Test
    public void testStringTaskOutput() {
        StringTaskOutput output = StringTaskOutput.success("success generated");
        assertThat(output.isSuccessful()).isTrue();
        assertThat(output.getOutput()).isEqualTo("success generated");
        assertThat(output.toString()).isEqualTo("StringTaskOutput{output='success generated', success=true, failureCause=null, failReason='null'}");
    }

    @Test
    public void testStringOutputFailure(){
        StringTaskOutput output = StringTaskOutput.fail("failed to generated");
        System.out.println(JacksonUtil.toJson(output));
        assertThat(output.isSuccessful()).isFalse();
        assertThat(output.getOutput()).isEqualTo("failed to generated");
        assertThat(output.toString()).isEqualTo("StringTaskOutput{output='failed to generated', success=false, failureCause=null, failReason='null'}");
    }

}
