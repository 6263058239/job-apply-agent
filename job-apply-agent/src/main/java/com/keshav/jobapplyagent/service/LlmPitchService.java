package com.keshav.jobapplyagent.service;

import com.keshav.jobapplyagent.model.CandidateProfile;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class LlmPitchService {
    private final ObjectMapper objectMapper=new ObjectMapper();
    private final HttpClient  httpClient= HttpClient.newHttpClient();

    public String generatePitch(String jobDescription, CandidateProfile profile)
    {
        String apiKey = System.getenv("Gemini_API_KEY");
        if(apiKey == null || apiKey.isBlank())
        {
            return getDefaultPitch(profile);
        }
        try{
            String prompt= String.format("""
                    You are assisting a software engineer applying for a job.
                    Candidate: %s, %d years experience,
                    Skills: %s,
                    Notice: %s,
                    Project: %s,
                    
                    Job Description snippet:
                    %s
                    Task: Write a crisp, tailored 3 to 4 sentence application pitch (under 75 words) explaining why this candidate is a direct fit for this specific JD. Return only plain text.
                    """,
                    profile.getName(),
                    profile.getExperienceYears(),
                    String.join(",",profile.getSkills()),
                    profile.getNoticePeriod(),
                    profile.getHighlightProject(),
                    jobDescription.replace("\"","'")

            );
            String requestBody= """
                    {
                    "contents":[{
                    "parts":[{"text":%s}]
                    }]
                    }
                    """.formatted(objectMapper.writeValueAsString(prompt));

            HttpRequest request=HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" +apiKey))
                    .header("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());
            return root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText().trim();
        } catch (Exception e) {
            System.err.println("LLM call failed, using fallback note: " + e.getMessage());
            return getDefaultPitch(profile);
        }
    }
    private String getDefaultPitch(CandidateProfile profile) {
        return String.format(
                "Hi Hiring Team, I bring %d years of hands-on experience in backend engineering with %s. I have built and delivered scalable systems like an %s. Being an %s, I can join immediately and contribute to your engineering goals.",
                profile.getExperienceYears(),
                String.join(", ", profile.getSkills().subList(0, 4)),
                profile.getHighlightProject(),
                profile.getNoticePeriod()
        );
    }
}
