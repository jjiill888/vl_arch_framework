// Learn more about Tauri commands at https://tauri.app/develop/calling-rust/
use tauri::Manager;
use tauri_plugin_global_shortcut::ShortcutState;

const LIB_HOME: &str = "http://server.15110y.top:8083";

#[tauri::command]
fn greet(name: &str) -> String {
    format!("Hello, {}! You've been greeted from Rust!", name)
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
        .plugin(
            tauri_plugin_global_shortcut::Builder::new()
                .with_shortcuts(["CTRL+Z", "COMMAND+Z"]) // Ctrl+Z handling on all platforms
                .unwrap()
                .with_handler(|app, _shortcut, event| {
                    if event.state == ShortcutState::Pressed {
                        let js = format!(
                            r#"(function() {{
    const iframe = document.querySelector('iframe');
    const win = iframe?.contentWindow;
    if (win) {{
      if (win.history.length > 1) {{
        win.history.back();
      }} else {{
        win.location.href = "{home}";
      }}
    }} else {{
      if (history.length > 1) {{
        history.back();
      }} else {{
        location.href = "{home}";
      }}
    }}
  }})();"#,
                            home = LIB_HOME
                        );
                        for window in app.webview_windows().values() {
                            let _ = window.eval(&js);
                        }
                    }
                })
                .build(),
        )
        .invoke_handler(tauri::generate_handler![greet])
        .on_page_load(|window, _| {
            let script = r#"
(function patchWindowOpen() {
  const origOpen = window.open;
  window.open = function (url, target) {
    if (typeof url === 'string' || url instanceof URL) {
      location.assign(String(url));
      return null;
    }
    return origOpen.apply(window, arguments);
  };
})();
document.addEventListener('click', (e) => {
  const anchor = e.target?.closest?.('a[href]');
  if (!anchor) return;
  const href = anchor.href;
  const target = anchor.target;
  if (target === '_blank') {
    e.preventDefault();
    e.stopPropagation();
    location.assign(href);
  }
}, true);
"#;

            let _ = window.eval(script);
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}