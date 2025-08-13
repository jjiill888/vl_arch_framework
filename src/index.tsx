/* @refresh reload */
import { render } from "solid-js/web";
import App from "./App";
import { getCurrentWebviewWindow } from "@tauri-apps/api/webviewWindow";
import { listen } from "@tauri-apps/api/event";

const LIB_HOME = "http://server.15110y.top:8083";

function injectCtrlZBack() {
  const js = `
    (function () {
      const isEditable = (el) => {
        if (!el) return false;
        const tag = el.tagName?.toLowerCase();
        return el.isContentEditable || tag === 'input' || tag === 'textarea' || tag === 'select';
      };

      window.addEventListener('keydown', (e) => {
        const ctrlZ = (e.ctrlKey && (e.key === 'z' || e.key === 'Z'));
        if (!ctrlZ) return;
        if (isEditable(e.target)) return;

        e.preventDefault();
        e.stopPropagation();

        if (history.length > 1) history.back();
        else location.href = ${JSON.stringify(LIB_HOME)};
      }, true);
    })();
  `;
  return js;
}

async function boot() {
  const wv = getCurrentWebviewWindow();
  await listen('tauri://webview-ready', () => wv.eval(injectCtrlZBack()));
  await listen('tauri://page-load', () => wv.eval(injectCtrlZBack()));
  wv.eval(injectCtrlZBack());
}

boot();

render(() => <App />, document.getElementById("root") as HTMLElement);
