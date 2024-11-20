import { Component, createSignal, onCleanup } from "solid-js";

const App: Component = () => {
  let iframeRef: HTMLIFrameElement | null = null;
  const [iframeUrl, setIframeUrl] = createSignal<string>('');

  // Optimization: Do not use setTimeout to simulate delay
  const loadIframe = (url: string) => {
    setIframeUrl(url);
    localStorage.setItem("iframeUrl", url); // Update cache
  };

  // Initialize URL: Get from cache or use default URL
  const initUrl = () => {
    const cachedUrl = localStorage.getItem("iframeUrl");
    return cachedUrl || "your-url";
  };

  // Load URL and iframe when the component is mounted
  const initialUrl = initUrl();
  loadIframe(initialUrl);  // Load URL immediately, no delay

  const onIframeLoad = () => {
    const iframeWindow = iframeRef?.contentWindow;

    if (iframeWindow) {
      // Add click event listeners to all links inside the iframe
      iframeWindow.document.addEventListener("click", (event) => {
        const target = event.target as HTMLAnchorElement;

        if (target.tagName === "A" && target.target === "_blank") {
          // If the link is supposed to open in a new window, prevent default behavior and load it in the iframe
          event.preventDefault();  // Prevent opening a new window
          loadIframe(target.href);  // Update iframe URL
        }
      });
    }
  };

  return (
    <div style={{ width: "100vw", height: "100vh", overflow: "hidden" }}>
      <iframe
        ref={iframeRef}
        src={iframeUrl()}
        style={{
          width: "100%",
          height: "100%",
          border: "none",
        }}
        onLoad={onIframeLoad}
        sandbox="allow-same-origin allow-scripts allow-forms"
      ></iframe>
    </div>
  );
};

export default App;