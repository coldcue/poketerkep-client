package hu.poketerkep.client.config.support;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceConfiguration {
    private final Map<String, String> tags;
    private final boolean master;

    public InstanceConfiguration(Instance ec2Instance) {
        List<Tag> tagList = ec2Instance.getTags();
        tags = new HashMap<>();

        for (Tag tag : tagList) {
            tags.put(tag.getKey(), tag.getValue());
        }

        this.master = "master".equals(tags.get("Role"));
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public boolean isMaster() {
        return master;
    }

}
