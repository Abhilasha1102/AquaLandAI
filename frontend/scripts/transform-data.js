// Transform bihar_full_data.json to bihar-admin.json format
const fs = require('fs');
const path = require('path');

// Read the comprehensive data
const fullData = JSON.parse(fs.readFileSync(path.join(__dirname, '../data/bihar_full_data.json'), 'utf8'));

// Transform to our format
const transformed = {
  state: "Bihar",
  stateCode: "BR",
  districts: fullData.districts.map(district => ({
    district: district.district_name,
    blocks: district.blocks.map(block => ({
      block: block.block_name,
      villages: block.villages.map(v => v.village_name)
    }))
  }))
};

// Write the transformed data
fs.writeFileSync(
  path.join(__dirname, '../data/bihar-admin.json'),
  JSON.stringify(transformed, null, 2),
  'utf8'
);

console.log(`✓ Transformed ${transformed.districts.length} districts`);
console.log(`✓ Total blocks: ${transformed.districts.reduce((sum, d) => sum + d.blocks.length, 0)}`);
console.log(`✓ Total villages: ${transformed.districts.reduce((sum, d) => sum + d.blocks.reduce((bsum, b) => bsum + b.villages.length, 0), 0)}`);
console.log(`✓ Saved to bihar-admin.json`);
