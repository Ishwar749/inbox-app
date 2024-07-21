package io.javabrains.inbox.controllers;

import io.javabrains.inbox.email.Email;
import io.javabrains.inbox.email.EmailRepository;
import io.javabrains.inbox.folders.Folder;
import io.javabrains.inbox.folders.FolderRepository;
import io.javabrains.inbox.folders.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private FolderService folderService;
    @Autowired
    private EmailRepository emailRepository;

    @GetMapping(value = "/email/{id}")
    public String getEmailPage(@PathVariable String id, @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null && principal.getAttribute("login") != null) {
            String loginId = principal.getAttribute("login");
            List<Folder> folders = folderRepository.findAllById(loginId);
            List<Folder> defaultFolders = folderService.fetchDefaultFolders(loginId);
            model.addAttribute("defaultFolders", defaultFolders);
            if (folders.size() > 0) {
                model.addAttribute("userFolders", folders);
            }
            try {
                UUID uuid = UUID.fromString(id);
                Optional<Email> optionalEmail = emailRepository.findById(uuid);
                if (optionalEmail.isPresent()) {
                    Email email = optionalEmail.get();
                    String toIds = String.join(", ", email.getTo());
                    model.addAttribute("toIds", toIds);
                    model.addAttribute("email", email);
                    return "email-page";
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return "inbox-page";
            }

        }
        return "index";
    }
}
