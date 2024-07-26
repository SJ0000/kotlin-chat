import { sleep } from "k6";
import stomp from "k6/x/stomp";

// ~/go/bin/k6 run xxx.js
export const options = {
  executor: "constant-vus",
  vus: 250,
  duration: "300s",
  gracefulStop: "3s",
};

export default function () {
  const client = stomp.connect({
    protocol: "wss",
    addr: "api.sj-sideproj-messenger.xyz",
    path: "/message-broker",
    timeout: "2s",
    message_send_timeout: "5s",
  });

  const groupChatId = __VU % 20;

  const subscription = client.subscribe(`/topic/group-chat/${groupChatId}`);
  console.log(`[VU ${__VU}] connected`);

  let count = 0;
  const startTime = Date.now();
  const duration = 5 * 60 * 1000;
  while (true) {
    const elapsed = Date.now() - startTime;
    if (elapsed >= duration) {
      console.log(`Duration over. VU ${__VU} finished.`);
      break;
    }

    let payload = {
      groupChatId: groupChatId,
      senderId: 1,
      content: `[VU ${__VU}] hello. iter = ${count}`,
      sentAt: new Date(),
    };
    client.send(
      "/app/group-message",
      "application/json",
      JSON.stringify(payload),
    );

    const msg = subscription.read();
    count++;
    sleep(1);
  }
}
