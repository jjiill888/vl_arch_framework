// Learn more about Tauri commands at https://tauri.app/develop/calling-rust/
#[tauri::command]
fn greet(name: &str) -> String {
    format!("Hello, {}! You've been greeted from Rust!", name)
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_shell::init())
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
