/**
 * Script de teste para verificar que as correções de segurança
 * não quebram a aplicação existente
 * 
 * Uso: node test-compatibility.js
 */

const fs = require('fs');
const path = require('path');

console.log('🔍 TESTE DE COMPATIBILIDADE - FatecRide Backend\n');
console.log('=' .repeat(60));

// ============================================
// 1️⃣ Verificar versões críticas
// ============================================
console.log('\n✅ 1. Verificando versões de dependências...\n');

const packageJsonPath = path.join(__dirname, 'package.json');
const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
const deps = packageJson.dependencies;

const criticalDeps = {
  'express': { current: deps.express, safe: '4.18.x', issue: false },
  'body-parser': { current: deps['body-parser'], safe: '1.20.x', issue: false },
  'mongoose': { current: deps.mongoose, safe: '^7.x ou ^8.x', issue: false },
  'jsonwebtoken': { current: deps.jsonwebtoken, safe: '^9.x', issue: false },
  'bcrypt': { current: deps.bcrypt, safe: '^6.x', issue: false }
};

let hasIssues = false;

for (const [pkg, info] of Object.entries(criticalDeps)) {
  const version = info.current || 'NÃO INSTALADO';
  const emoji = version.includes('2.2.0') || version.includes('5.1.0') ? '❌' : '✅';
  console.log(`${emoji} ${pkg}: ${version}`);
  
  if (version.includes('2.2.0') || version.includes('5.1.0')) {
    hasIssues = true;
  }
}

// ============================================
// 2️⃣ Verificar arquivo .env
// ============================================
console.log('\n✅ 2. Verificando variáveis de ambiente...\n');

const envPath = path.join(__dirname, '.env');
if (fs.existsSync(envPath)) {
  const envContent = fs.readFileSync(envPath, 'utf8');
  const secretMatch = envContent.match(/SECRET=(.+)/);
  
  if (secretMatch) {
    const secret = secretMatch[1].trim();
    const isWeak = secret.length < 20 || secret === 'my-secret-key-from-video';
    const emoji = isWeak ? '⚠️' : '✅';
    console.log(`${emoji} SECRET length: ${secret.length} caracteres`);
    
    if (isWeak) {
      console.log('   ⚠️  Recomendação: Usar SECRET com 32+ caracteres aleatórios\n');
      hasIssues = true;
    }
  }
} else {
  console.log('❌ Arquivo .env não encontrado\n');
  hasIssues = true;
}

// ============================================
// 3️⃣ Verificar app.js
// ============================================
console.log('✅ 3. Verificando configuração de segurança...\n');

const appJsPath = path.join(__dirname, 'app.js');
const appContent = fs.readFileSync(appJsPath, 'utf8');

// Verificar CORS
const hasCorsMissing = appContent.includes('origin:"*"');
const corsEmoji = hasCorsMissing ? '⚠️' : '✅';
console.log(`${corsEmoji} CORS: ${hasCorsMissing ? 'Aberto (*) - Recomenda-se whitelist' : 'Configurado'}`);

// Verificar Helmet
const hasHelmet = appContent.includes('helmet') || appContent.includes('Helmet');
const helmetEmoji = hasHelmet ? '✅' : '⚠️';
console.log(`${helmetEmoji} Helmet: ${hasHelmet ? 'Instalado' : 'NÃO INSTALADO - Recomendado'}`);

// Verificar Rate Limiting
const hasRateLimit = appContent.includes('rateLimit') || appContent.includes('rate-limit');
const rateLimitEmoji = hasRateLimit ? '✅' : '⚠️';
console.log(`${rateLimitEmoji} Rate Limiting: ${hasRateLimit ? 'Instalado' : 'NÃO INSTALADO - Recomendado'}`);

if (hasCorsMissing || !hasHelmet || !hasRateLimit) {
  hasIssues = true;
}

// ============================================
// 4️⃣ Verificar TokenService
// ============================================
console.log('\n✅ 4. Verificando TokenService...\n');

const tokenServicePath = path.join(__dirname, 'service', 'TokenService.js');
const tokenContent = fs.readFileSync(tokenServicePath, 'utf8');

const hasConsoleSecret = tokenContent.includes('console.log') && tokenContent.includes('SECRET');
const tokenEmoji = hasConsoleSecret ? '❌' : '✅';
console.log(`${tokenEmoji} Logging de SECRET: ${hasConsoleSecret ? 'console.log detectado - REMOVER!' : 'OK'}`);

if (hasConsoleSecret) {
  hasIssues = true;
}

// ============================================
// 5️⃣ Verificar estrutura de pastas
// ============================================
console.log('\n✅ 5. Verificando estrutura do projeto...\n');

const requiredDirs = [
  'controller',
  'service',
  'models',
  'database'
];

const requiredFiles = [
  'app.js',
  'package.json',
  '.env',
  'swagger.json'
];

let structureOk = true;

for (const dir of requiredDirs) {
  const dirPath = path.join(__dirname, dir);
  const exists = fs.existsSync(dirPath);
  const emoji = exists ? '✅' : '❌';
  console.log(`${emoji} Pasta: ${dir}/`);
  if (!exists) structureOk = false;
}

for (const file of requiredFiles) {
  const filePath = path.join(__dirname, file);
  const exists = fs.existsSync(filePath);
  const emoji = exists ? '✅' : '❌';
  console.log(`${emoji} Arquivo: ${file}`);
  if (!exists) structureOk = false;
}

// ============================================
// 6️⃣ Testar conexão (básico)
// ============================================
console.log('\n✅ 6. Verificando disponibilidade de módulos...\n');

const modules = [
  'express',
  'mongoose',
  'jsonwebtoken',
  'bcrypt',
  'cors',
  'dotenv',
  'body-parser'
];

for (const mod of modules) {
  try {
    require.resolve(mod);
    console.log(`✅ ${mod}: instalado`);
  } catch (e) {
    console.log(`❌ ${mod}: NÃO ENCONTRADO`);
    hasIssues = true;
  }
}

// ============================================
// Resultado Final
// ============================================
console.log('\n' + '='.repeat(60));

if (hasIssues) {
  console.log('\n⚠️  PROBLEMAS DETECTADOS\n');
  console.log('Recomendações:');
  console.log('  1. npm audit fix          # Corrigir vulnerabilidades');
  console.log('  2. node test-compatibility.js  # Rodar este teste novamente');
  console.log('\n📖 Ver SECURITY-AUDIT.md para instruções detalhadas');
  process.exit(1);
} else {
  console.log('\n✅ TUDO OK!\n');
  console.log('Seu projeto está pronto. Recomendações adicionais:');
  console.log('  - Implementar Rate Limiting com express-rate-limit');
  console.log('  - Adicionar validação com Joi');
  console.log('  - Configurar CORS com whitelist de domínios');
  console.log('  - Instalar Helmet para headers de segurança');
  console.log('\n📖 Ver README.md para guia completo de segurança');
  process.exit(0);
}
