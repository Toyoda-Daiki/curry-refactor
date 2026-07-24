'use strict';

document.addEventListener('DOMContentLoaded', function () {
  let addressCount = 1;
  const container = document.getElementById('addressContainer');

  // リファクタリング課題#13 「＋住所を追加」ボタンで行を動的に追加
  document.getElementById('addAddressBtn').addEventListener('click', function () {
    const newRow = document.createElement('div');
    newRow.className = 'address-row';
    newRow.innerHTML = `
          <div class="row">
            <div class="col-xs-5">
              <input type="text" name="addresses[${addressCount}].name" class="form-control" placeholder="氏名" />
            </div>
            <div class="col-xs-5">
              <input type="text" name="addresses[${addressCount}].tel" class="form-control"
                placeholder="電話番号（例：090-1234-5678）" />
            </div>
            <div class="col-xs-2 text-center">
              <button type="button" class="remove-btn">×</button>
            </div>
          </div>
          <div class="row" style="margin-top: 8px;">
            <div class="col-xs-10">
              <input type="text" name="addresses[${addressCount}].address" class="form-control"
                placeholder="住所（例：千葉県浦安市舞浜1-1）" />
            </div>
          </div>
        `;
    // innerHTML += ではなく appendChild を使う（既存リスナーを保持するため）
    container.appendChild(newRow);
    addressCount++;
  });

  // リファクタリング課題#36 イベント委譲で削除ボタンを処理
  // 親要素にリスナーを1つだけ登録し、動的追加した要素にも対応する
  container.addEventListener('click', function (e) {
    const removeBtn = e.target.closest('.remove-btn');
    if (removeBtn) {
      // 最低1行は残す
      const rows = container.querySelectorAll('.address-row');
      if (rows.length > 1) {
        removeBtn.closest('.address-row').remove();
      } else {
        alert('最低1件の住所が必要です');
      }
    }
  });
});
