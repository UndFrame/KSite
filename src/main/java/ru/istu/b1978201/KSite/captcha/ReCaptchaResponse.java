package ru.istu.b1978201.KSite.captcha;

/**
 * Класс - data, который хранит внутри себя результат проверки капчи
 */
public class ReCaptchaResponse {

    private boolean success;
    private String challengeTs;
    private String hostName;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getChallengeTs() {
        return challengeTs;
    }

    public void setChallengeTs(String challengeTs) {
        this.challengeTs = challengeTs;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return "ReCaptchaResponse{" +
                "success=" + success +
                ", challengeTs='" + challengeTs + '\'' +
                ", hostName='" + hostName + '\'' +
                '}';
    }
}
