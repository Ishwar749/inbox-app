package io.javabrains.inbox;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.javabrains.inbox.email.Email;
import io.javabrains.inbox.email.EmailRepository;
import io.javabrains.inbox.emaillist.EmailListItem;
import io.javabrains.inbox.emaillist.EmailListItemKey;
import io.javabrains.inbox.emaillist.EmailListItemRepository;
import io.javabrains.inbox.folders.Folder;
import io.javabrains.inbox.folders.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
@RestController
public class InboxApp {

    @Autowired
    FolderRepository folderRepository;
    @Autowired
    EmailListItemRepository emailListItemRepository;
    @Autowired
    EmailRepository emailRepository;

    public static void main(String[] args) {
        SpringApplication.run(InboxApp.class, args);
    }

    /*
     * This is necessary to have the spring boot app use the Astra secure bundle
     * to connect to the database
     */
    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

    @PostConstruct
    public void init() {
        folderRepository.save(new Folder("Ishwar749", "From_Rahul", "blue"));
        folderRepository.save(new Folder("Ishwar749", "Pull_Requests", "green"));
        folderRepository.save(new Folder("Ishwar749", "Build_Mails", "yellow"));

        for(int i =0; i<10; i++){
            EmailListItemKey key = new EmailListItemKey();
            key.setId("Ishwar749");
            key.setLabel("Inbox");
            key.setTimeUUID(Uuids.timeBased());

            EmailListItem item = new EmailListItem();
            item.setKey(key);
            item.setTo(Arrays.asList("Ishwar749"));
            item.setSubject("Subject: "+i);
            item.setUnread(true);

            emailListItemRepository.save(item);

            Email email = new Email();
            email.setId(key.getTimeUUID());
            email.setFrom("Ishwar749");
            email.setSubject(item.getSubject());
            email.setBody("Body: "+i);
            email.setTo(item.getTo());
            emailRepository.save(email);
        }
    }

}
